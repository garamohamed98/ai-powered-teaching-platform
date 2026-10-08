import {Component, Input, OnInit} from '@angular/core';
import {ExerciseContent} from '../../models/exercise-types/exercise-content.model';
import {MultipleChoiceContent} from '../../models/exercise-types/multiple-choice.model';
import {RadioButton} from 'primeng/radiobutton';
import {FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-multiple-choice-attempt-form',
  imports: [
    RadioButton,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './multiple-choice-attempt-form.component.html',
  styleUrl: './multiple-choice-attempt-form.component.scss'
})
export class MultipleChoiceAttemptFormComponent implements OnInit {

  @Input({required:true}) content!:ExerciseContent;
  @Input({required:true}) form!:FormGroup;

  multipleChoiceContent!: MultipleChoiceContent;


  ngOnInit() {
    this.multipleChoiceContent = this.content as MultipleChoiceContent;
  }

}
