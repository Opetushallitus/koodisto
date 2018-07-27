"use strict";

import {OPH_ORG} from "./auth";

export class ChildOpener {
    constructor() {
        "ngInject";
    }

    open(data) {
        data.open = !data.open;
        if (data.open) {
            var iter = function(children){
                if(children) {
                    children.forEach(function(child){
                        child.open = true;
                        iter(child.children);
                    });
                }
            };

            iter(data.children);
        }
    }
}

export class OrganisaatioTreeModel {
    constructor(OrganizationChildrenByOid, childOpener) {
        "ngInject";
        this.OrganizationChildrenByOid = OrganizationChildrenByOid;
        this.childOpener = childOpener;
        var instance = {};
        instance.model = {};
    }

    init(organizations) {
        this.model = {};
        this.model.organisaatiot = [];
        this.model.numHits = 0;
        this.organizationsToInitFrom = organizations;
        this.searchStr = "";
    }

    resetSearch() {
        if (this.model.originalOrganizations && this.model.originalOrganizations.length > 0){
            this.model.organisaatiot =  this.model.originalOrganizations;
            this.model.numHits = this.calculateMatchingOrgs(this.model.originalOrganizations);
        } else {
            this.organizationsToInitFrom.forEach(function(organization) {
                this.OrganizationChildrenByOid.get({oid: organization}, function(result) {
                    result.organisaatiot.forEach((org) => {
                        this.model.organisaatiot.push(org);
                    });
                    this.model.numHits += result.numHits;
                });
            });
        }
    }

    calculateMatchingOrgs(organizations) {
        var amount = organizations.length;
        function calculate(children) {
            amount += children.length;
            children.forEach(function(child) {
                calculate(child.children);
            });
        }
        organizations.forEach(function(org) {
            calculate(org.children);
        });
        return amount;
    }

    search(searchStr) {

        var matchingOrgs = new Array();

        function matchesSearch(organization) {
            function matchesTranslation(organization2, language) {
                return organization2.nimi[language] && organization2.nimi[language].toLowerCase().indexOf(searchStr) > -1;
            }
            return matchesTranslation(organization, 'fi');
        }

        function recursivelyAddMatchingOrganizations(organization) {
            if (matchesSearch(organization)) {
                matchingOrgs.push(organization);
            } else {
                organization.children.forEach(function(child) {
                    recursivelyAddMatchingOrganizations(child);
                });
            }
        }

        searchStr = searchStr.toLowerCase();

        if (!this.model.originalOrganizations || this.model.originalOrganizations.length < 1) {
            this.model.originalOrganizations = this.model.organisaatiot;
        }

        this.model.originalOrganizations.forEach(function(organization) {
            recursivelyAddMatchingOrganizations(organization);
        });

        this.model.organisaatiot = matchingOrgs;
        this.model.numHits = this.calculateMatchingOrgs(matchingOrgs);

        if (this.model.organisaatiot.length < 4) {
            this.model.organisaatiot.forEach((data) => {
                this.openChildren(data);
            });
        }
    };

    openChildren(data) {
        this.childOpener.open(data);
    };
}

export class OrganisaatioOPHTreeModel {
    constructor(Organizations, OrganizationByOid, childOpener) {
        "ngInject";
        this.Organizations = Organizations;
        this.OrganizationByOid = OrganizationByOid;
        this.childOpener = childOpener;

        this.model = {};
        this.searchStr = "";
    }

    init() {
        this.model = {};
    }

    resetSearch() {
        this.OrganizationByOid.get({oid: OPH_ORG}, function(result) {
            this.model.organisaatiot = [result];
            this.model.numHits = 1;
        });
    }

    search(searchStr) {
        this.Organizations.get({"searchStr": searchStr, "skipparents": true}, (result) => {
            this.model = result;
            if (this.model.organisaatiot.length < 4) {
                this.model.organisaatiot.forEach((data) => {
                    this.openChildren(data);
                });
            }
        });
    }

    openChildren(data) {
        this.childOpener.open(data);
    }

}

export class OrganisaatioTreeController {
    constructor($scope, authService, OrganisaatioTreeModel, organisaatioOPHTreeModel) {
        "ngInject";
        if (!$scope.orgTree) {
            authService.updateOph(SERVICE_NAME).then(function() {
                $scope.orgTree = organisaatioOPHTreeModel;
            }, function() {
                $scope.orgTree = OrganisaatioTreeModel;
            });

            authService.getOrganizations(SERVICE_NAME).then(function(organizations) {
                $scope.orgTree.init(organizations);
            });

            $scope.$watch('orgTree.searchStr', function() {
                if($scope.orgTree.searchStr.length > 2) {
                    $scope.orgTree.search($scope.orgTree.searchStr);
                } else if ($scope.orgTree.searchStr.length < 1){
                    $scope.orgTree.resetSearch();
                }
            });
        }

        $scope.openChildren = function(data) {
            $scope.orgTree.openChildren(data);
        };

        $scope.clear = function(){
            $scope.orgTree.searchStr = '';
            $scope.orgTree.resetSearch();
        };

    }
}

export class ModalInstanceCtrl {
    constructor($scope, $modalInstance) {
        "ngInject";
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        $scope.organisaatioSelector = function (data) {
            $modalInstance.close(data);
        };
    }
}
