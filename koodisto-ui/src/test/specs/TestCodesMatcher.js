import {CodesMatcher} from "../../main/webapp/html/codes/codesMatcher";
import {expect} from 'chai';

describe("Codes Matcher test", function() {

    let matcher;

    const codesToMatch = {
        'koodistoUri': 'koodistouri',
        'latestKoodistoVersio': {
            'metadata': [{
                'kieli': 'FI',
                'nimi': 'Ääkköinen'
            }, {
                'kieli': 'SV',
                'nimi': 'with spåces'
            }, {
                'kieli': 'EN',
                'nimi': 'In cAPs'
            }]
        }
    };

    beforeEach(function() {
        matcher = new CodesMatcher();
    });

    it("returns true if filter is undefined", function() {
        expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, null)).to.be.ok;
    });

    it("returns false if filter's length is below 2", function() {
        expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "a")).to.not.be.ok;
    });

    it("codes does not match filter", function() {
        expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "koodistourr")).to.not.be.ok;
    });

    it("koodiUri matches filter", function() {
        expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "kood")).to.be.ok;
    });

    it("finnish name with scandic letters smatches filter", function() {
        expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "äkkö")).to.be.ok;
    });

    it("swedish name with scandic letters and spaces smatches filter", function() {
        expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "h spåce")).to.be.ok;
    });

    it("english name with capital letters and spaces smatches filter", function() {
        expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "nca")).to.be.ok;
        expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "incaps")).to.be.ok;
        expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "INCAPS")).to.be.ok;
    });

});
