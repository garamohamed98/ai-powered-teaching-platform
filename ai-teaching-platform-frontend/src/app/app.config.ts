import { ApplicationConfig, provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {provideAnimations} from '@angular/platform-browser/animations';
import {providePrimeNG} from 'primeng/config';
import Aura from '@primeng/themes/aura';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {MessageService} from 'primeng/api';
import {errorInterceptor} from './error.interceptor';
import {camelToSnakeInterceptor} from './camel-to-snake.interceptor';
import {snakeToCamelInterceptor} from './features/snake-to-camel.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }), provideRouter(routes),
    provideAnimations(),
    providePrimeNG({
      theme: {
        preset: Aura,
        options: {
          darkModeSelector: '.dark-mode'
        }
      }
    }),
    provideHttpClient(
      withInterceptors([errorInterceptor, camelToSnakeInterceptor, snakeToCamelInterceptor])
    ),
    MessageService
  ]
};
