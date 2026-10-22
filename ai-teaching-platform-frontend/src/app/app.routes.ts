import { Routes } from '@angular/router';
import { authGuardFn} from '@auth0/auth0-angular';
import {MainLayoutComponent} from './layout/main-layout/main-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: '',
        pathMatch: 'full', loadComponent:()=>
          import("./pages/home/home.component")
            .then(m=>m.HomeComponent)
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
          import('./pages/not-found/not-found.component').then(
            (m) => m.NotFoundComponent
          )
      },
      {
        path: '**',
        loadComponent: () =>
          import('./pages/not-found/not-found.component').then(
            (m) => m.NotFoundComponent
          )
      }
    ]
  }
];
