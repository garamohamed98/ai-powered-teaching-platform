import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadChildren:()=>
      import('./features/courses/courses.routes').then(
        (m)=> m.COURSES_ROUTES
      )
  }
];
