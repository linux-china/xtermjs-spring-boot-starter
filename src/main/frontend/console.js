//xterm imports
import {Terminal} from 'xterm';
import {WebLinksAddon} from 'xterm-addon-web-links';
import {FitAddon} from 'xterm-addon-fit';
import {RSocketAddon} from './xterm-addon-rsocket';
import "xterm/css/xterm.css";

class XtermConsole extends HTMLElement {
    constructor() {
        super();
        let rsocketUrl = this.getAttribute('rsocket');
        if (rsocketUrl.startsWith("/")) { //without schema and host
            let schema = "ws://";
            if (document.location.protocol.startsWith("https")) {
                schema = "wss://";
            }
            rsocketUrl = schema + document.location.host + rsocketUrl;
        }
        let container = document.createElement('div');
        container.style.cssText = 'width: 100%; height: 100%;';
        this.append(container);
        //initialize xterm
        let term = new Terminal();
        term.prompt = () => {
            term.write('\r\n$');
        };
        //load addons
        term.loadAddon(new WebLinksAddon());
        let fitAddon = new FitAddon();
        term.loadAddon(fitAddon);
        term.loadAddon(new RSocketAddon(rsocketUrl));
        term.open(container);
        fitAddon.fit();
        //welcome banner
        let hint = title || 'Welcome xterm with RSocket.';
        term.writeln(hint);
        term.prompt();
        term.focus();
        this.terminal = term;
    }
}

window.customElements.define('xterm-console', XtermConsole);
