import {HttpErrorResponse, HttpInterceptorFn} from '@angular/common/http';
import {catchError, throwError} from 'rxjs';
import {inject} from '@angular/core';
import {MessageService} from 'primeng/api';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {

  const messageService = inject(MessageService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {

      console.error('HTTP Error:', error);
      console.log("the error is working")

      messageService.add(
        {
          severity: 'error',
          summary: 'Error',
          sticky: true,
          detail: "A server error occurred",
        }
      )

      if (error.status === 400) {
        console.log('Bad Request');

      }

      if (error.status === 401) {
        console.log('Unauthorized');
      }

      if (error.status === 500) {
        console.log('Server Error');
      }

      return throwError(() => error);
    })
  );
};
