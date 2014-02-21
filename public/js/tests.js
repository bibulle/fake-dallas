angular.module('tests', [
        'testBackend', 'uploadManager', 'ngSanitize'
    ]).config(function ($routeProvider) {
    $routeProvider.when('/', {
        controller: ListCtrl,
        templateUrl: '/assets/partials/list.html'
    }).otherwise({
        redirectTo: '/'
    });
}).directive('message',function ($timeout, $log) {
    return {
        controller: function ($scope, $element, $rootScope) {
            $scope.message = {
                text: "",
                type: "info",
                date: new Date()
            }
            $element.alert();

            $scope.$watch('message', function (newVal, oldVal) {
                if (newVal.text != "") {
                    if (angular.isDefined($scope.messageTimout)) {
                        $timeout.cancel($scope.messageTimout);
                    }
                    // $element.show();
                    if ($scope.message.type == 'error') {
                        $scope.messageTimout = $timeout(function () {
                            $scope.message.text = "";
                        }, 25000, true);
                    } else {
                        $scope.messageTimout = $timeout(function () {
                            $scope.message.text = "";
                        }, 5000, true);
                    }
                }
            }, true);
        },
    }
}).directive('statusClass',function () {
    return function (scope, elem, attrs) {
        scope.$watch(attrs.statusClass, function (value) {
            if (value.status.match(/^OK.*/))
                elem.addClass('statusOK');
            else
                elem.removeClass('statusOK');
        });
    };
}).run(function ($rootScope, $location, $log, $timeout) {
    $rootScope.conf = {}

    $rootScope.setMessageSuccess = function (message) {
        $rootScope.message.text = message;
        $rootScope.message.date = new Date();
        $rootScope.message.type = 'success';
    };
    $rootScope.setMessageInfo = function (message) {
        $rootScope.message.text = message;
        $rootScope.message.date = new Date();
        $rootScope.message.type = 'info';
    };
    $rootScope.setMessageError = function (message) {
        $rootScope.message.text = message;
        $rootScope.message.date = new Date();
        $rootScope.message.type = 'error';
    };
    $rootScope.appendMessageError = function (message) {
        if ($rootScope.message.text != "") {
            $rootScope.message.text += "<BR>";
        }
        $rootScope.message.text += message;
        $rootScope.message.date = new Date();
        $rootScope.message.type = 'error';
    };

    // Manage the WebSocket
    // $rootScope.connectWebSocket = function() {
    // var ws = new
    // WebSocket("ws://"+$location.host()+":"+$location.port()+"/webSocket");
    //
    // ws.onopen = function() {
    // console.log("Socket has been opened");
    // $rootScope.refreshNeeded = true;
    // $rootScope.$broadcast('properties_refresh_needed');
    // }
    // ws.onclose = function() {
    // console.log("Socket has been closed");
    // $rootScope.setMessageError("Connection to server lost");
    // $timeout(function() {
    // $rootScope.connectWebSocket();
    // }, 5000, true);
    //
    // }
    // ws.onmessage = function(message) {
    // var data = JSON.parse(message.data);
    // console.log("Socket message received");
    // if (data.action == 'noRefreshNeeded') {
    // $rootScope.refreshNeeded = false;
    // }
    // $rootScope.$broadcast('properties_'+data.action, data.property);
    // }
    //
    // $rootScope.$on('properties_refresh_needed', function(event) {
    // if ($rootScope.refreshNeeded) {
    // msg = {
    // action: "refresh",
    // lastUpdateDate: $rootScope.lasUpdateDate,
    // lastUpdateId: $rootScope.lasUpdateId,
    // count: 30
    // };
    // ws.send(JSON.stringify(msg));
    //
    // }
    // });
    // }
    // $rootScope.connectWebSocket();

});

function ListCtrl($scope, TestBackend, $rootScope, $timeout, $log) {

    $scope.Math = window.Math;

    $rootScope.lasUpdateDate = 0;

    $scope.tests = [];

    // $scope.tests = TestBackend.query();

    // start pooling
    (function tick() {
        TestBackend.query(function (lst) {
            $scope.tests = lst;
            $timeout(tick, 10 * 1000);
        });
    })();

    $scope.deleteTest = function (obj) {
        TestBackend.delete(obj, function () {
            $scope.tests = TestBackend.query();
        });

    };

    // $scope.$watch('properties', function (newValue, oldValue, scope) {
    // alert("properties changed "+newval)
    // }, true);
}
