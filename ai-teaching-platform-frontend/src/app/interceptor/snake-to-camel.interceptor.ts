import {HttpInterceptorFn, HttpResponse} from '@angular/common/http';
import {map} from 'rxjs';

function snakeToCamel(obj: any): any {
  if (Array.isArray(obj)) return obj.map(snakeToCamel);
  if (obj !== null && typeof obj === 'object') {
    return Object.fromEntries(
      Object.entries(obj).map(([k, v]) => [
        k.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase()),
        snakeToCamel(v),
      ])
    );
  }
  return obj;
}

export const snakeToCamelInterceptor: HttpInterceptorFn = (req, next) =>
  next(req).pipe(
    map(event =>
      event instanceof HttpResponse
        ? event.clone({ body: snakeToCamel(event.body) })
        : event
    )
  );
