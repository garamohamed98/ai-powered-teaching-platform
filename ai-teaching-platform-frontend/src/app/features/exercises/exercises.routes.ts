import {Routes} from '@angular/router';
import {ExerciseDetailsComponent} from './pages/exercise-details/exercise-details.component';
import {ExerciseAttemptComponent} from './pages/exercise-attempt/exercise-attempt.component';
import {ExerciseAttemptResultComponent} from './pages/exercise-attempt-result/exercise-attempt-result.component';

export const EXERCISES_ROUTES: Routes = [
  {
    path:':id/exercise-details',
    component: ExerciseDetailsComponent
  },
  {
    path:':id/attempt',
    component:ExerciseAttemptComponent
  },
  {
    path:':id/attempt-result',
    component:ExerciseAttemptResultComponent
  }

]
