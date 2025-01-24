const environments = ["hahtuva", "dev", "qa", "prod"] as const;
type EnvironmentName = (typeof environments)[number];

const defaultConfig = {
  zoneName: "",
};

export type Config = typeof defaultConfig;

export function getEnvironment(): EnvironmentName {
  const env = process.env.ENV;
  if (!env) {
    throw new Error("ENV environment variable is not set");
  }
  if (!contains(environments, env)) {
    throw new Error(`Invalid environment name: ${env}`);
  }
  return env as EnvironmentName;
}

function contains(arr: readonly string[], value: string): boolean {
  return arr.includes(value);
}

export function getConfig(): Config {
  const env = getEnvironment();
  return { hahtuva, dev, qa, prod }[env];
}

export const hahtuva: Config = {
  ...defaultConfig,
  zoneName: "hahtuva.koodisto.opintopolku.fi",
};

export const dev: Config = {
  ...defaultConfig,
  zoneName: "dev.koodisto.opintopolku.fi",
};

export const qa: Config = {
  ...defaultConfig,
  zoneName: "qa.koodisto.opintopolku.fi",
};

export const prod: Config = {
  ...defaultConfig,
  zoneName: "prod.koodisto.opintopolku.fi",
};
