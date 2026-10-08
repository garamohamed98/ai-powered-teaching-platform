import {Component, Input, OnInit} from '@angular/core';
import {ExerciseContent} from '../../models/exercise-types/exercise-content.model';
import {FillInBlankContent} from '../../models/exercise-types/fill-in-blank.model';
import {InputText} from 'primeng/inputtext';
import {FormGroup, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-fill-in-blank-attempt-form',
  imports: [
    InputText,
    ReactiveFormsModule
  ],
  templateUrl: './fill-in-blank-attempt-form.component.html',
  styleUrl: './fill-in-blank-attempt-form.component.scss'
})
export class FillInBlankAttemptFormComponent implements OnInit {
  @Input({required:true}) content!:ExerciseContent;
  @Input({required:true}) form!:FormGroup;

  fillInBlankContent!: FillInBlankContent;

  ngOnInit() {
    this.fillInBlankContent = this.content as FillInBlankContent;
  }
}
