var app = angular.module('calcApp', []);

app.factory('HttpClient', function($http, $timeout) {
    var evalService = {
        result: null,
        eval: function (expr) {
            $http.post('http://localhost:9000/request#fragment', {expr: expr}).
                success(function(data, status, headers, config) {
                    $timeout(function() {
                        evalService.result = data.result;
                    });
                }).
                error(function(data, status, headers, config) {
                });
        }
    };
    return evalService;
});

//app.factory('WsClient', function($http, $timeout) {
//    var ws = new WebSocket("ws://localhost:9000/feed");
//    var evalService = {
//        result: null,
//        eval: function (expr) {
//            ws.send(JSON.stringify({expr: expr}));
//        }
//    };
//    ws.onmessage = function(event) {
//        $timeout(function() {
//            evalService.result = JSON.parse(event.data).result;
//        });
//    };
//    return evalService;
//});

app.controller('Eval', function($scope, $http, $timeout, HttpClient) {
    $scope.eval = function() {
        HttpClient.eval($scope.expr);
    };

});

app.controller('Calc', function($scope, $http, $timeout, HttpClient) {

    $scope.result = null;

    $scope.$watch(
        function() {
            return HttpClient.result;
        },
        function(result) {
            $scope.result = result;
        }
    );

});