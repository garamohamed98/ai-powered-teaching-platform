import {Component, Input, OnInit} from '@angular/core';
import {ExerciseAttemptResult} from '../../../models/exercise-attempt-result.model';
import {FillInBlankComparedAttempt} from '../../../models/compared-attempt/fill-in-blank-compared-attempt.model';
import {Divider} from 'primeng/divider';

@Component({
  selector: 'app-fill-in-blank-attempt-result',
  imports: [
    Divider
  ],
  templateUrl: './fill-in-blank-attempt-result.component.html',
  styleUrl: './fill-in-blank-attempt-result.component.scss'
})
export class FillInBlankAttemptResultComponent implements OnInit {

  @Input({required: true}) attemptResult!: ExerciseAttemptResult;

  comparedAttempt!: FillInBlankComparedAttempt;

  ngOnInit() {
    this.comparedAttempt = this.attemptResult.comparedAnswer as FillInBlankComparedAttempt;
    console.log(this.comparedAttempt);
  }

}
