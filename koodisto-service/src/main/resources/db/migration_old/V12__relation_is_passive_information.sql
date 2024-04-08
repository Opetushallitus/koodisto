ALTER TABLE koodinsuhde ADD COLUMN ylakoodistapassiivinen boolean default false NOT NULL;
ALTER TABLE koodinsuhde ADD COLUMN alakoodistapassiivinen boolean default false NOT NULL;
ALTER TABLE koodistonsuhde ADD COLUMN ylakoodistostapassiivinen boolean default false NOT NULL;
ALTER TABLE koodistonsuhde ADD COLUMN alakoodistostapassiivinen boolean default false NOT NULL;