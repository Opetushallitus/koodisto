import {urls} from 'oph-urls-js';

export const READ = "_READ";
export const UPDATE = "_READ_UPDATE";
export const CRUD = "_CRUD";
export const OPH_ORG = "1.2.246.562.10.00000000001";


export class MyRolesModel {
    constructor($q, $http) {
        "ngInject";
        this.$q = $q;
        this.$http = $http;

        this.deferred = null;
    }

    getMyRoles() {
        if (this.deferred) {
            return this.deferred;
        }
        this.deferred= this.$q.defer();

        var instance = {};
        instance.myroles = [];

        this.$http.get(urls.url("cas.myroles")).success(function(result) {
            instance.myroles = result;
            this.deferred.resolve(instance);
        });

        return this.deferred.promise;
    }
}

export class AuthService {
    controller($q, $http, $timeout, myRolesModel) {
        "ngInject";
        this.$q = $q;
        this.$http = $http;
        this.$timeout = $timeout;
        this.myRolesModel = myRolesModel;
    }

    // organisation check
    readAccess(service,org,model) {
        if ( model.myroles.indexOf(service + READ + "_" + org) > -1 ||
            model.myroles.indexOf(service + UPDATE + "_" + org) > -1 ||
            model.myroles.indexOf(service + CRUD + "_" + org) > -1) {
            return true;
        }
    };

    updateAccess(service,org,model) {

        if ( model.myroles.indexOf(service + UPDATE + "_" + org) > -1 ||
            model.myroles.indexOf(service + CRUD + "_" + org) > -1) {
            return true;
        }
    };

    crudAccess(service,org,model) {

        if ( model.myroles.indexOf(service + CRUD + "_" + org) > -1) {
            return true;
        }
    };

    anyUpdateAccess(service,model) {
        var found = false;
        model.myroles.forEach(function(role) {
            if ( role.indexOf(service + UPDATE) > -1 ||
                role.indexOf(service + CRUD) > -1) {
                found = true;
            }
        });
        return found;
    };

    anyCrudAccess(service,model) {
        var found = false;
        model.myroles.forEach(function(role) {
            if ( role.indexOf(service + CRUD) > -1) {
                found = true;
            }
        });
        return found;
    };


    accessCheck(service, orgOid, accessFunction) {
        var deferred = this.$q.defer();

        this.myRolesModel.getMyRoles().then(function(model){
            this.$http.get(urls.url("organisaatio-service.parentoids", orgOid)).success((result) => {
                var found = false;
                result.split("/").forEach(function(org){
                    if (accessFunction(service, org, model)){
                        found = true;
                    }
                });
                if (found) {
                    deferred.resolve();
                } else {
                    deferred.reject();
                }
            });
        });

        return deferred.promise;
    };

    // OPH check -- voidaan ohittaa organisaatioiden haku
    ophRead(service,model) {
        return (model.myroles.indexOf(service + READ + "_" + OPH_ORG) > -1
            || model.myroles.indexOf(service + UPDATE + "_" + OPH_ORG) > -1
            || model.myroles.indexOf(service + CRUD + "_" + OPH_ORG) > -1);
    };

    ophUpdate(service,model) {
        return (model.myroles.indexOf(service + UPDATE + "_" + OPH_ORG) > -1
            || model.myroles.indexOf(service + CRUD + "_" + OPH_ORG) > -1);
    };

    ophCrud(service,model) {
        return (model.myroles.indexOf(service + CRUD + "_" + OPH_ORG) > -1);
    };

    ophAccessCheck(service, accessFunction) {
        var deferred = this.$q.defer();

        this.myRolesModel.getMyRoles().then((model) => {
            if(accessFunction(service, model)) {
                deferred.resolve();
            } else {
                deferred.reject();
            }
        });

        return deferred.promise;
    };

    readOrg(service, orgOid) {
        return this.accessCheck(service, orgOid, this.readAccess);
    }

    updateOrg(service, orgOid) {
        return this.accessCheck(service, orgOid, this.updateAccess);
    }

    crudOrg(service, orgOid) {
        return this.accessCheck(service, orgOid, this.crudAccess);
    }

    readOph(service) {
        return this.ophAccessCheck(service, this.ophRead);
    }

    updateOph(service) {
        return this.ophAccessCheck(service, this.ophUpdate);
    }

    crudOph(service) {
        return this.ophAccessCheck(service, this.ophCrud);
    }

    crudAny(service) {
        return this.ophAccessCheck(service, this.anyCrudAccess);
    }

    updateAny(service) {
        return this.ophAccessCheck(service, this.anyUpdateAccess);
    }

    getOrganizations(service) {
        var deferred = this.$q.defer();

        this.myRolesModel.getMyRoles().then(function(model){
            this.organizations = [];

            model.myroles.forEach(function(role) {
                // TODO: refaktor
                var org;
                if(role.indexOf(service + "_CRUD_") > -1) {
                    org = role.replace(service + "_CRUD_", '');
                } else if(role.indexOf(service + "_READ_UPDATE_") > -1) {
                    org = role.replace(service + "_READ_UPDATE_", '');
                } else if(role.indexOf(service + "_READ_UPDATE") === -1 && role.indexOf(service + "_READ_") > -1) {
                    org = role.replace(service + "_READ_", '');
                }

                if(org && organizations.indexOf(org) === -1) {
                    organizations.push(org);
                }
            });

            deferred.resolve(organizations);
        });
        return deferred.promise;
    }
}
