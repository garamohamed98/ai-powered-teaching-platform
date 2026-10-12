import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ExerciseAttemptResult} from '../../models/exercise-attempt-result.model';
import {Exercise} from '../../models/exercise.model';
import {ExerciseSummaryComponent} from '../../components/exercise-summary/exercise-summary.component';
import {AttemptResultComponent} from '../../components/attempt-result/attempt-result/attempt-result.component';

@Component({
  selector: 'app-exercise-attempt-result',
  imports: [
    ExerciseSummaryComponent,
    AttemptResultComponent
  ],
  templateUrl: './exercise-attempt-result.component.html',
  styleUrl: './exercise-attempt-result.component.scss'
})
export class ExerciseAttemptResultComponent implements OnInit {

  private router = inject(Router);

  attemptResult!: ExerciseAttemptResult;
  exercise!: Exercise;

  constructor(private route:ActivatedRoute) {
  }

  ngOnInit() {
    const id = this.route.snapshot.params['id'];

    if(!id){
      void this.router.navigate(['/'])
    }

    const passedAttemptResult = history.state?.['exerciseAttemptResult'] as ExerciseAttemptResult;

    if(!passedAttemptResult){
      //TODO: fetch the exercise attempt result by id
    }else{
      this.attemptResult = passedAttemptResult;
      this.exercise = {
        id: this.attemptResult.exerciseId,
        title: this.attemptResult.title,
        type: this.attemptResult.type,
        instructions: this.attemptResult.instructions,
        lessonList: [],
        content: {}
      }
    }

  }


}
