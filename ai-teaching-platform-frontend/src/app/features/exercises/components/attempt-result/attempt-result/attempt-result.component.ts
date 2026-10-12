import {Component, Input} from '@angular/core';
import {Card} from 'primeng/card';
import {ExerciseAttemptResult} from '../../../models/exercise-attempt-result.model';
import {
  FillInBlankAttemptResultComponent
} from '../fill-in-blank-attempt-result/fill-in-blank-attempt-result.component';
import {
  MultipleChoiceAttemptResultComponent
} from '../multiple-choice-attempt-result/multiple-choice-attempt-result.component';

@Component({
  selector: 'app-attempt-result',
  imports: [
    Card,
    FillInBlankAttemptResultComponent,
    MultipleChoiceAttemptResultComponent
  ],
  templateUrl: './attempt-result.component.html',
  styleUrl: './attempt-result.component.scss'
})
export class AttemptResultComponent {

  @Input({required:true}) attemptResult!: ExerciseAttemptResult;

  toTimeStringFormat(time: number): string {
    let result ="";

    const ms = time % 1000;
    result = ms + "ms"

    const totalSeconds = Math.floor(time / 1000);
    const ss = totalSeconds % 60;
    if(ss > 0){
      result = ss +"s : " + result;
    }

    const mn = Math.floor(totalSeconds / 60) % 60;
    if(mn > 0){
      result = mn + "mn : " + result;
    }

    const hh = Math.floor(totalSeconds / 3600);
    if(hh > 0){
      result = hh + "h : " + result;
    }

    return result;
  }
}
