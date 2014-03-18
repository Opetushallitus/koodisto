"use strict";
app.factory('OrganisaatioTreeModel', function(Organizations, AuthService, OrganizationChildrenByOid) {

    return (function() {
        var instance = {};
        instance.model = {};
        instance.searchStr = "";

        var oph = {
            "oid" : "1.2.246.562.10.00000000001",
            "parentOidPath" : "1.2.246.562.10.00000000001",
            "nimi" : {
                "fi" : "OPH",
                "sv" : "OPH",
                "en" : "OPH"
            },
            "children": []
        };
        instance.init = function(organizations) {
            instance.model = {};
            organizations.forEach(function(organization) {
        	OrganizationChildrenByOid.get({oid: organization}, function(result) {

        	    if(!instance.model.organisaatiot) {
        		instance.model.organisaatiot = [];
        		instance.model.numHits = 0;
        	    }
        	    
        	    result.organisaatiot.forEach(function(org) {
        		instance.model.organisaatiot.unshift(org);
        	    });
        	    instance.model.numHits = result.numHits;
        	});
            });
        };

        instance.search = function(searchStr) {
            searchStr = searchStr.toLowerCase();
            if (!instance.model.originalOrganizations) {
        	instance.originalOrganizations = instance.model.organisaatiot;
            }
            
            function matchesTranslation(organization, language) {
        	return organization.nimi[language] && organization.nimi[language].toLowerCase().indexOf(searchStr) > -1;
            }
            function matchesSearch(organization) {
        	return matchesTranslation(organization, 'fi') || matchesTranslation(organization, 'sv') || matchesTranslation(organization, 'en');
            }
            
            var matchingOrgs = new Array();
            function recursivelyAddMatchingOrganizations(organization) { 
        	if (matchesSearch(organization)) {
        	    matchingOrgs.unshift(organization);
        	} else {
        	    organization.children.forEach(function(child) {        	    
        		recursivelyAddMatchingOrganizations(child);
        	    });
        	}
            }
            
            instance.originalOrganizations.forEach(function(organization) {
        	recursivelyAddMatchingOrganizations(organization);
            });
            
            instance.model.organisaatiot = matchingOrgs;
            instance.model.numHits = matchingOrgs.length;
            
//            var params = {"searchStr": searchStr};
//            Organizations.get(params, function(result){
//                instance.model = result;
            if(instance.model.organisaatiot.length < 4) {
        	instance.model.organisaatiot.forEach(function(data){
        	    instance.openChildren(data);
        	});
            }
//            });

        }

        instance.openChildren = function(data) {
            data.open = !data.open;
            if(data.open) {

                var iter = function(children){
                    if(children) {
                        children.forEach(function(child){
                            child.open = true;
                            iter(child.children);
                        });
                    }
                }

                iter(data.children);
            }
        };

        return instance;
    })();

});

function OrganisaatioTreeController($scope, AuthService, OrganisaatioTreeModel) {
    $scope.orgTree = OrganisaatioTreeModel;
    $scope.$watch('orgTree.searchStr', debounce(function() {
        if($scope.orgTree.searchStr.length > 2) {
            OrganisaatioTreeModel.search($scope.orgTree.searchStr);
        } else {
            AuthService.getOrganizations(serviceName).then(function(organizations) {
        	OrganisaatioTreeModel.init(organizations);
            });
            
        }
    }, 500));

    function debounce(fn, delay) {
        var timer = null;
        return function () {
            var context = this, args = arguments;
            clearTimeout(timer);
            timer = setTimeout(function () {
                fn.apply(context, args);
            }, delay);
        };
    }

    $scope.openChildren = function(data) {
        OrganisaatioTreeModel.openChildren(data);
    }

    $scope.clear = function(){
        OrganisaatioTreeModel.model = {};
        OrganisaatioTreeModel.searchStr = '';
    }


}


var ModalInstanceCtrl = function ($scope, $modalInstance, OrganisaatioTreeModel) {

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };


    $scope.organisaatioSelector = function(data) {
        $modalInstance.close(data);
    };
};