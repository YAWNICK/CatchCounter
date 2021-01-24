using Toybox.WatchUi;

class CatchCounterDelegate extends WatchUi.BehaviorDelegate {

    function initialize() {
        BehaviorDelegate.initialize();
    }

    function onMenu() {
        WatchUi.pushView(new Rez.Menus.MainMenu(), new CatchCounterMenuDelegate(), WatchUi.SLIDE_UP);
        return true;
    }

}