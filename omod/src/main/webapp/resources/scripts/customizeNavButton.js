jq(document).ready(function () {
    emr.loadMessages([
        "general.next",
        "general.previous"
    ], () => customizeNavButtons());

});

function customizeNavButtons() {
    customizeNavButton('#registration #next-button', 'general.next');
    customizeNavButton('#registration-section-form #next-button', 'general.next');
    customizeNavButton('#registration #prev-button', 'general.previous');
    customizeNavButton('#registration-section-form #prev-button', 'general.previous');
}

function customizeNavButton(selector, message) {
    jq(selector).html(emr.message(message)).removeClass('confirm').addClass('nav-button');
}
