app.controller('MainController', ['$scope', '$window', '$http', function($scope, $window, $http) { 
	
	$scope.username = '';
	$scope.password = '';
	$scope.baseUrl = 'https://ehrscape.code4health.org/rest/v1/'; // 'https://cdr.code4health.org/rest/v1/'; //default
	
	$scope.response; // = 'Response';
	
	$scope.testFunction = function() {
		$window.alert("Clicked!");
	};
	
	$scope.getSession = function() {
		$http.post("api/myresource/getSession")
		.then(function(response) {
            var res = JSON.stringify(response);
            //$window.alert(res);
            console.log(res);
        });
	};
	
	$scope.provisionSingle = function() {
		console.log($scope.username);
		console.log($scope.password);
		console.log($scope.baseUrl);
		var body = JSON.stringify({username: $scope.username, password:$scope.password, baseUrl: $scope.baseUrl})
		console.log(body);
		$http.post("api/provision/single-provision-no-demographic", body)
		.then(function(response) {
            var res = JSON.stringify(response);
            //$window.alert(res);
            // console.log(res);
            //$scope.response = res;
            var obj = JSON.parse(res);
            $scope.response = JSON.stringify(obj.data,null,2);
        });
	};
	
	$scope.provisionSingleWithMarandDemographic = function() {
		console.log($scope.username);
		console.log($scope.password);
		console.log($scope.baseUrl);
		var body = JSON.stringify({username: $scope.username, password:$scope.password, baseUrl: $scope.baseUrl})
		console.log(body);
		$http.post("api/provision/single-provision-marand", body)
		.then(function(response) {
            var res = JSON.stringify(response);
            //$window.alert(res);
            $scope.response = res;
        });
	};
	
	$scope.provisionSingleWithFhirDemographic = function() {
		console.log($scope.username);
		console.log($scope.password);
		console.log($scope.baseUrl);
		var body = JSON.stringify({username: $scope.username, password:$scope.password, baseUrl: $scope.baseUrl})
		console.log(body);
		$http.post("api/provision/single-provision-fhir", body)
		.then(function(response) {
            var res = JSON.stringify(response);
            //$window.alert(res);
            //console.log(res);
            $scope.response = res;
        });
	};
	
}]);

	