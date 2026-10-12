import {Component, Input, OnInit} from '@angular/core';
import {ExerciseAttemptResult} from '../../../models/exercise-attempt-result.model';
import {MultipleChoiceComparedAttempt} from '../../../models/compared-attempt/multiple-choice-compared-attempt.model';

@Component({
  selector: 'app-multiple-choice-attempt-result',
  imports: [],
  templateUrl: './multiple-choice-attempt-result.component.html',
  styleUrl: './multiple-choice-attempt-result.component.scss'
})
export class MultipleChoiceAttemptResultComponent implements OnInit {

  @Input({required: true}) attemptResult!: ExerciseAttemptResult;

  comparedAttempt!: MultipleChoiceComparedAttempt;

  ngOnInit() {
    this.comparedAttempt = this.attemptResult.comparedAnswer as MultipleChoiceComparedAttempt;
    console.log(this.comparedAttempt);
  }
}
