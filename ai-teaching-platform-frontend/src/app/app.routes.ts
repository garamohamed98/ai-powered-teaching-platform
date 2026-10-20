import { Routes } from '@angular/router';
import { authGuardFn} from '@auth0/auth0-angular';
import {MainLayoutComponent} from './layout/main-layout/main-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'courses' },
      {
        path: 'login',
        loadComponent: ()=>
          import("./features/users/pages/login/login.component")
            .then(m => m.LoginComponent),
      },
      {
        path: 'courses',
        canActivate: [authGuardFn],
        loadChildren: () =>
          import('./features/courses/courses.routes').then(
            (m) => m.COURSES_ROUTES
          )
      },
      {
        path: 'exercises',
        loadChildren: () =>
          import('./features/exercises/exercises.routes').then(
            (m) => m.EXERCISES_ROUTES
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
    ]
  }
];
