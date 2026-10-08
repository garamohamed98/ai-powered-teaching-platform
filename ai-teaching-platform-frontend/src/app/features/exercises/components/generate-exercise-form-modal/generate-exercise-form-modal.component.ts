import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {Dialog} from 'primeng/dialog';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MultiSelect} from 'primeng/multiselect';
import {Lesson} from '../../models/lesson.model';
import {Exercise, ExerciseType} from '../../models/exercise.model';
import {Select} from 'primeng/select';
import {Button} from 'primeng/button';
import {GenerateExerciseDto} from '../../models/generate-exercise.dto';
import {ExercisesService} from '../../services/exercises.service';
import {MessageService} from 'primeng/api';

@Component({
  selector: 'app-generate-exercise-form-modal',
  imports: [
    Dialog,
    ReactiveFormsModule,
    MultiSelect,
    Select,
    Button
  ],
  templateUrl: './generate-exercise-form-modal.component.html',
  styleUrl: './generate-exercise-form-modal.component.scss'
})
export class GenerateExerciseFormModalComponent {

  @Input({required:true}) isVisible!: boolean;
  @Output() isVisibleChange = new EventEmitter<boolean>();
  @Input({required:true}) lessons!: Lesson[];
  @Output() exerciseGenerated = new EventEmitter<void>();

  private formBuilder = inject(FormBuilder);
  private exercisesService = inject(ExercisesService);
  private messageService = inject(MessageService);

  exerciseTypes :{
    label: string;
    value: ExerciseType;
  }[]= [
    { label: 'Multiple Choice', value: 'MULTIPLE_CHOICE' },
    { label: 'Fill in blank', value:'FILL_IN_BLANK'}
  ];

  form: FormGroup = this.formBuilder.group({
    exerciseType:[
      null as ExerciseType | null,
      Validators.required
    ],
    lessons:[
      [] as String[],
    ],
  });

  close(): void {
    this.isVisible = false;
    this.isVisibleChange.emit(false);
  }


  submit(): void {
    if(!this.form.valid) return;

    const {exerciseType, lessons} = this.form.value;

    const generateExerciseDto: GenerateExerciseDto = {
      type: exerciseType.value,
      lessonIdList: lessons.map((lesson:Lesson)=>lesson.id)
    }

    this.exercisesService.generateExercise(generateExerciseDto).subscribe({
      next:(res:Exercise)=>{
        console.log("Exercise generated successfully");
        this.messageService.add({
          severity: 'success',
          summary: 'New Exercise is generated successfully',
          detail: `Exercise has been created successfully related to lessons ${lessons.map((lesson:Lesson) => lesson.title).join(', ')}`,
        })
        this.exerciseGenerated.emit();
        this.close();
      }
    })

  }
}
