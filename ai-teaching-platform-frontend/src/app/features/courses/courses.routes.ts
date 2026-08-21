import {Routes} from '@angular/router';
import {CoursesComponent} from './pages/courses/courses.component';
import {LessonsComponent} from './pages/lessons/lessons.component';

export const COURSES_ROUTES: Routes = [
  {
    path: '',
    component: CoursesComponent
  },
  {
    path:':id/lessons',
    component: LessonsComponent
  }
]
