using Toybox.WatchUi;
using Toybox.System;
using Toybox.Communications;

class CatchCounterDelegate extends WatchUi.BehaviorDelegate {

    var parentView;

    function initialize(view) {
        BehaviorDelegate.initialize();
        parentView = view;
    }

    // function onMenu() {
    //     WatchUi.pushView(new Rez.Menus.MainMenu(), new CatchCounterMenuDelegate(), WatchUi.SLIDE_UP);
    //     return true;
    // }

    function onKey(keyEvent) {
        var key = keyEvent.getKey();
        if (key == KEY_ENTER) {
            if (parentView.sending == false) {
                parentView.beginSending();
            } else {
                parentView.endSending();
            }
        }
    }

}