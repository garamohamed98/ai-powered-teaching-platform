import {Component, Input} from '@angular/core';
import {Card} from 'primeng/card';
import {Exercise, getExerciseTypeLabel} from '../../models/exercise.model';
import {FillInBlankContentComponent} from '../content/fill-in-blank-content/fill-in-blank-content.component';
import {MultipleChoiceContentComponent} from '../content/multiple-choice-content/multiple-choice-content.component';

@Component({
  selector: 'app-exercise-infos',
  imports: [
    Card,
    FillInBlankContentComponent,
    MultipleChoiceContentComponent
  ],
  templateUrl: './exercise-infos.component.html',
  styleUrl: './exercise-infos.component.scss'
})
export class ExerciseInfosComponent {

  @Input({required:true}) exercise!: Exercise;


  protected readonly getExerciseTypeLabel = getExerciseTypeLabel;
}
