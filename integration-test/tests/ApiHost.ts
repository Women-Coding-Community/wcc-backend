class ApiHost {
  static get(): string {
    return process.env.API_HOST || 'http://localhost:8080';
  }
}

export default ApiHost;