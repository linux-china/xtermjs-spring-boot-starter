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
            } else if (code === 127) { //backspace
                if (this.commandLine.length > 0) {
                    let lastCode = this.commandLine.charCodeAt(this.commandLine.length - 1);
                    if (lastCode > 255) { //utf-8 character
                        this.terminal.write('\b \b\b \b');
                    } else {
                        this.terminal.write('\b \b');
                    }
                    this.commandLine = this.commandLine.substr(0, this.commandLine.length - 1);
                }
            } else if (code < 32) { // Control

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
            if (this.commandLine === "clear") { //clear screen
                this.terminal.clear();
                this.terminal.reset();
            } else if (this.commandLine === "exit" || this.commandLine === "quit" ) { //close window or tab
                this.terminal.dispose();
                this.rsocketClient.close();
                window.close();
            } else {
                this.commandFlux.onNext({data: new Buffer(this.commandLine)});
            }
        }
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