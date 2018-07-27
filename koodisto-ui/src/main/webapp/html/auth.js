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
        this.deferred = this.$q.defer();

        const instance = {};
        instance.myroles = [];

        this.$http.get(urls.url("cas.myroles")).then((result) => {
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

        // organisation check
        this.readAccess = (service, org, model) => {
            if ( model.myroles.indexOf(service + READ + "_" + org) > -1 ||
                model.myroles.indexOf(service + UPDATE + "_" + org) > -1 ||
                model.myroles.indexOf(service + CRUD + "_" + org) > -1) {
                return true;
            }
        };

        this.updateAccess = (service, org, model) => {
            if ( model.myroles.indexOf(service + UPDATE + "_" + org) > -1 ||
                model.myroles.indexOf(service + CRUD + "_" + org) > -1) {
                return true;
            }
        };

        this.crudAccess = (service, org, model) => {
            if ( model.myroles.indexOf(service + CRUD + "_" + org) > -1) {
                return true;
            }
        };

        this.anyUpdateAccess = (service, model) => {
            let found = false;
            model.myroles.forEach((role) => {
                if ( role.indexOf(service + UPDATE) > -1 ||
                    role.indexOf(service + CRUD) > -1) {
                    found = true;
                }
            });
            return found;
        };

        this.anyCrudAccess = (service, model) => {
            let found = false;
            model.myroles.forEach((role) => {
                if ( role.indexOf(service + CRUD) > -1) {
                    found = true;
                }
            });
            return found;
        };


        this.accessCheck = (service, orgOid, accessFunction) => {
            const deferred = this.$q.defer();

            this.myRolesModel.getMyRoles().then((model) => {
                this.$http.get(urls.url("organisaatio-service.parentoids", orgOid)).then((result) => {
                    let found = false;
                    result.split("/").forEach((org) =>{
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
        this.ophRead = (service,model) => {
            return (model.myroles.indexOf(service + READ + "_" + OPH_ORG) > -1
                || model.myroles.indexOf(service + UPDATE + "_" + OPH_ORG) > -1
                || model.myroles.indexOf(service + CRUD + "_" + OPH_ORG) > -1);
        };

        this.ophUpdate = (service,model) => {
            return (model.myroles.indexOf(service + UPDATE + "_" + OPH_ORG) > -1
                || model.myroles.indexOf(service + CRUD + "_" + OPH_ORG) > -1);
        };

        this.ophCrud = (service,model) => {
            return (model.myroles.indexOf(service + CRUD + "_" + OPH_ORG) > -1);
        };

        this.ophAccessCheck = (service, accessFunction) => {
            const deferred = this.$q.defer();

            this.myRolesModel.getMyRoles().then((model) => {
                if (accessFunction(service, model)) {
                    deferred.resolve();
                } else {
                    deferred.reject();
                }
            });

            return deferred.promise;
        };

        this.readOrg = (service, orgOid) => {
            return this.accessCheck(service, orgOid, this.readAccess);
        };

        this.updateOrg = (service, orgOid) => {
            return this.accessCheck(service, orgOid, this.updateAccess);
        };

        this.crudOrg = (service, orgOid) => {
            return this.accessCheck(service, orgOid, this.crudAccess);
        };

        this.readOph = (service) => {
            return this.ophAccessCheck(service, this.ophRead);
        };

        this.updateOph = (service) => {
            return this.ophAccessCheck(service, this.ophUpdate);
        };

        this.crudOph = (service) => {
            return this.ophAccessCheck(service, this.ophCrud);
        };

        this.crudAny = (service) => {
            return this.ophAccessCheck(service, this.anyCrudAccess);
        };

        this.updateAny = (service) => {
            return this.ophAccessCheck(service, this.anyUpdateAccess);
        };

        this.getOrganizations = (service) => {
            const deferred = this.$q.defer();

            this.myRolesModel.getMyRoles().then((model) => {
                this.organizations = [];

                model.myroles.forEach((role) => {
                    // TODO: refaktor
                    let org;
                    if (role.indexOf(service + "_CRUD_") > -1) {
                        org = role.replace(service + "_CRUD_", '');
                    } else if (role.indexOf(service + "_READ_UPDATE_") > -1) {
                        org = role.replace(service + "_READ_UPDATE_", '');
                    } else if (role.indexOf(service + "_READ_UPDATE") === -1 && role.indexOf(service + "_READ_") > -1) {
                        org = role.replace(service + "_READ_", '');
                    }

                    if (org && organizations.indexOf(org) === -1) {
                        organizations.push(org);
                    }
                });

                deferred.resolve(organizations);
            });
            return deferred.promise;
        }
    }

}
