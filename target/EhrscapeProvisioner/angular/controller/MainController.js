app.controller('MainController', ['$scope', '$window', '$http', function($scope, $window, $http) { 
	
	$scope.testFunction = function() {
		$window.alert("Clicked!");
	};
	
	$scope.getSession = function() {
		$http.post("http://localhost:8080/EhrscapeProvisioner/api/myresource/getSession")
		.then(function(response) {
            var res = JSON.stringify(response);
            //$window.alert(res);
            console.log(res);
        });
	};

}]);