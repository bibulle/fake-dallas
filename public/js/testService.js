// Modules to managed timeline
angular.module('testBackend', ['ngResource']).
  factory('TestBackend', function($resource) {
                                                     
    var TestBackend= $resource('/test/:id');
    
    return TestBackend;
  });
