import {HttpErrorResponse, HttpInterceptorFn} from '@angular/common/http';
import {catchError, throwError} from 'rxjs';
import {inject} from '@angular/core';
import {MessageService} from 'primeng/api';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {

  const messageService = inject(MessageService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {

      console.error('HTTP Error:', error);

      if (error.status === 0) {
        messageService.add({
          severity: 'error',
          summary: 'Connection Error',
          detail: 'Unable to reach the server. Please check your connection.',
          sticky: true,
        });
      }

      if (error.status === 400) {
        messageService.add({
          severity: 'warn',
          summary: 'Invalid Request',
          detail: 'Please check your input and try again.',
          sticky: false,
        });
      }

      if (error.status === 401) {
        messageService.add({
          severity: 'warn',
          summary: 'Session Expired',
          detail: 'Please log in again.',
          sticky: false,
        });
      }

      if (error.status === 403) {
        messageService.add({
          severity: 'warn',
          summary: 'Access Denied',
          detail: 'You don\'t have permission to do this.',
          sticky: false,
        });
      }

      if (error.status === 404) {
        messageService.add({
          severity: 'info',
          summary: 'Not Found',
          detail: 'The requested resource could not be found.',
          sticky: false,
        });
      }

      if (error.status === 500) {
        messageService.add({
          severity: 'error',
          summary: 'Server Error',
          detail: 'Something went wrong on our end. Please try again later.',
          sticky: true,
        });
      }

      return throwError(() => error);
    })
  );
};
