// Modules to managed timeline
angular.module('testBackend', ['ngResource']).
  factory('TestBackend', function($resource, $location) {
                                                     
    var TestBackend= $resource('/test/:id');
    
    return TestBackend;
  });
