function flattenObject(obj: any, prefix = ''): any {
  const flattened: any = {};

  Object.keys(obj).forEach((key) => {
    if (typeof obj[key] === 'object' && obj[key] !== null) {
      Object.assign(flattened, flattenObject(obj[key], prefix));
    } else {
      flattened[prefix + key] = obj[key];
    }
  });
  return flattened;
}

export default flattenObject;
