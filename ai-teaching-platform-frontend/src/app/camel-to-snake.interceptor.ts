import { HttpInterceptorFn } from '@angular/common/http';

function camelToSnake(obj: any): any {
  if (Array.isArray(obj)) return obj.map(camelToSnake);
  if (obj !== null && typeof obj === 'object') {
    return Object.fromEntries(
      Object.entries(obj).map(([k, v]) => [
        k.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`),
        camelToSnake(v),
      ])
    );
  }
  return obj;
}

export const camelToSnakeInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.body) {
    return next(req.clone({ body: camelToSnake(req.body) }));
  }
  return next(req);
};
