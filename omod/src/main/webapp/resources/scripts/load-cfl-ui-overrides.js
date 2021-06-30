const CFL_UI_ROOT = '/openmrs/owa/cfl-ui/';

const head = document.head || document.getElementsByTagName('head')[0];

const style = document.createElement('link');
style.rel = 'stylesheet preload';
style.href = CFL_UI_ROOT + 'overrides.css';
style.as = 'style';

head.appendChild(style);

const SCRIPT_HREF = CFL_UI_ROOT + 'overrides.js';

const scriptPreload = document.createElement('link');
scriptPreload.rel = 'script preload';
scriptPreload.href = SCRIPT_HREF;
scriptPreload.as = 'script';

head.appendChild(scriptPreload);

const script = document.createElement('script');
script.src = SCRIPT_HREF;

head.appendChild(script);
