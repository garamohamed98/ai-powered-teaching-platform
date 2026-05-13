import {Routes} from '@angular/router';
import {CoursesComponent} from './pages/courses/courses.component';
import {CourseDetailsComponent} from './pages/course-details/course-details.component';

export const COURSES_ROUTES: Routes = [
  {
    path: '',
    component: CoursesComponent
  },
  {
    path:':id',
    component: CourseDetailsComponent
  }
]
