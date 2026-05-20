import { Routes } from '@angular/router';
import {NotFoundComponent} from './features/not-found/not-found.component';

export const routes: Routes = [
  {
    path: 'courses',
    loadChildren: () =>
      import('./features/courses/courses.routes').then(
        (m) => m.COURSES_ROUTES
      )
  },
  {
    path: 'not-found',
    loadComponent: () =>
      import('./features/not-found/not-found.component').then(
        (m) => m.NotFoundComponent
      )
  },
  {
    path: '**',
    loadComponent: () =>
      import('./features/not-found/not-found.component').then(
        (m) => m.NotFoundComponent
      )
  }
];
