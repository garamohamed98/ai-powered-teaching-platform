import {Routes} from '@angular/router';
import {ExerciseDetailsComponent} from './pages/exercise-details/exercise-details.component';

export const EXERCISES_ROUTES: Routes = [
  {
    path:':id/exercise-details',
    component: ExerciseDetailsComponent
  }
]
