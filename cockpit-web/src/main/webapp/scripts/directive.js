(function (){
	'use strict';

	angular.module('cockpit').directive('projectId', function() {
		return {
			restrict: 'C',
			replace: true,
			transclude: true,
			template: '<select ng-model="consumerGroup" ng-options="consumerGroup.groupName for consumerGroup in consumerGroups">'
					+ '	</select> '
					+ ' <button type="submit" class="btn">add group</button> ',
			link: function(scope, element, attrs) {
				$visibleG = false;
			}
		}
	});
})();
