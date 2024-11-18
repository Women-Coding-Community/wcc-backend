class EnvVariables {
  static getApiTest(): string {
    return process.env.API_TESTS || 'http://localhost:8080';
  }
}

export default EnvVariables;