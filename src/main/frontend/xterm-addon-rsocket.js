import {
    BufferEncoders,
    encodeAndAddWellKnownMetadata,
    MESSAGE_RSOCKET_COMPOSITE_METADATA,
    MESSAGE_RSOCKET_ROUTING,
    RSocketClient,
} from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';
import {FlowableProcessor} from "rsocket-flowable";

const maxRSocketRequestN = 2147483647;
const keepAlive = 60000;
const lifetime = 180000;
const dataMimeType = "text/plain";
const metadataMimeType = MESSAGE_RSOCKET_COMPOSITE_METADATA.string;

export class RSocketAddon {

    constructor(url, options) {
        // current command line
        this.commandLine = '';
        // commands history
        this.history = new HistoryController(100);
        // command flux
        this.commandFlux = new FlowableProcessor({});
        this.rsocketClient = new RSocketClient({
            setup: {
                keepAlive,
                lifetime,
                dataMimeType,
                metadataMimeType,
            },
            transport: new RSocketWebSocketClient(
                    {wsCreator: () => new WebSocket(url), debug: true},
                    BufferEncoders,
            ),
        });
    }

    activate(terminal) {
        this.terminal = terminal;
        //attach key listener to parse command line
        this.attachKeyListener();
        //attach executeLine for terminal
        terminal.executeLine = () => {
            this.triggerCommand();
        };
        // open rsocket connection
        this.rsocketClient.connect().then(rsocket => {
            this.rsocket = rsocket;
            rsocket.requestChannel(this.commandFlux).subscribe({
                onComplete: () => console.log('Terminal completed'),
                onError: error => console.log(`Communication error:${error.message}`),
                onNext: payload => this.outputRemoteResult(payload),
                onSubscribe: sub => sub.request(maxRSocketRequestN),
            });
            //first payload with routing key
            this.commandFlux.onNext(
                    {
                        data: new Buffer(''),
                        metadata: encodeAndAddWellKnownMetadata(
                                Buffer.alloc(0),
                                MESSAGE_RSOCKET_ROUTING,
                                RSocketAddon.routingKey('xterm.shell'),
                        )
                    });
        });
    }

    attachKeyListener() {
        //term key event for Input & Enter & Backspace
        this.terminal.onData((data) => {
            let code = data.charCodeAt(0);
            if (code === 13) { // enter
                this.triggerCommand();
                this.terminal.prompt();
                this.commandLine = '';
            } else if (code === 127 || code === 8) { //backspace
                if (this.commandLine.length > 0) {
                    let lastCode = this.commandLine.charCodeAt(this.commandLine.length - 1);
                    if (lastCode > 255) { //utf-8 character
                        this.terminal.write('\b \b\b \b');
                    } else {
                        this.terminal.write('\b \b');
                    }
                    this.commandLine = this.commandLine.substr(0, this.commandLine.length - 1);
                }
            } else if (code === 27) { // escape
                switch (data.substr(1)) {
                    case "[A": // Up arrow
                        this.terminal.write("\r\x1B[K$");
                        let previous = this.history.getPrevious();
                        if (previous != null) {
                            this.terminal.write(previous);
                            this.commandLine = previous;
                        }
                        break;
                    case "[B": // Down arrow
                        this.terminal.write("\r\x1B[K$");
                        let next = this.history.getNext();
                        if (next != null) {
                            this.terminal.write(next);
                            this.commandLine = next;
                        }
                        break;
                }
            } else if (code < 32) { // Control
                if (code === 21) { // Control+u to clear line
                    this.terminal.write("\r\x1B[K$");
                    this.commandLine = "";
                } else if (code === 12) { //ctrl+l to clear screen
                    this.clearScreen();
                    this.terminal.write("\r\x1B[K$");
                    this.commandLine = "";
                }
            } else { // Visible
                this.commandLine += data;
                this.terminal.write(data);
            }
        });
    }

    // trigger command execute
    triggerCommand() {
        if (this.rsocket == null) {  //rsocket not available
            this.terminal.write('\r\n\u001b[31mFailed to connect RSocket backend, please check your service! \u001b[39m');
            return;
        }
        if (this.commandLine.trim().length > 0) {
            this.history.push(this.commandLine.trim());
            if (this.commandLine === "clear") { //clear screen
                this.clearScreen();
            } else if (this.commandLine === "exit" || this.commandLine === "quit") { //close window or tab
                this.terminal.dispose();
                this.rsocketClient.close();
                window.close();
            } else {
                this.commandFlux.onNext({data: new Buffer(this.commandLine)});
            }
        }
    }

    clearScreen() {
        this.terminal.clear();
        this.terminal.reset();
    }

    //output remote result
    outputRemoteResult(payload) {
        if (payload.data != null && payload.data.length > 0) {
            this.terminal.write('\b \b');
            this.terminal.write(payload.data);
            this.terminal.prompt();
        }
    }

    dispose() {
        this.rsocketClient.close();
    }

    static routingKey(key) {
        let buffer = Buffer.alloc(1 + key.length);
        buffer.writeInt8(key.length, 0);
        buffer.write(key, 1);
        return buffer
    }
}

class HistoryController {
    constructor(size) {
        this.size = size;
        this.entries = [];
        this.cursor = 0;
    }

    /**
     * Push an entry and maintain ring buffer size
     */
    push(entry) {
        // Skip duplicate entries
        const lastEntry = this.entries[this.entries.length - 1];
        if (entry === lastEntry) return;
        // Keep track of entries
        this.entries.push(entry);
        if (this.entries.length > this.size) {
            this.entries.pop(0);
        }
        this.cursor = this.entries.length;
    }

    /**
     * Rewind history cursor on the last entry
     */
    rewind() {
        this.cursor = this.entries.length;
    }

    /**
     * Returns the previous entry
     */
    getPrevious() {
        const idx = Math.max(0, this.cursor - 1);
        this.cursor = idx;
        return this.entries[idx];
    }

    /**
     * Returns the next entry
     */
    getNext() {
        const idx = Math.min(this.entries.length, this.cursor + 1);
        this.cursor = idx;
        return this.entries[idx];
    }
}