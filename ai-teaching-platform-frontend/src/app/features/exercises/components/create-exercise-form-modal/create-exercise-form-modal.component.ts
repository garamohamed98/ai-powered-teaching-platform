import {Component, EventEmitter, inject, Input, Output, signal} from '@angular/core';
import {Dialog} from 'primeng/dialog';
import {Form, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {InputText} from 'primeng/inputtext';
import {Select} from 'primeng/select';
import {Exercise, ExerciseType} from '../../models/exercise.model';
import {MultipleChoiceFormComponent} from '../../forms/multiple-choice-form/multiple-choice-form.component';
import {FillInBlankFormComponent} from '../../forms/fill-in-blank-form/fill-in-blank-form.component';
import {Button} from 'primeng/button';
import {MultiSelect} from 'primeng/multiselect';
import {Lesson} from '../../models/lesson.model';
import {ToggleSwitch} from 'primeng/toggleswitch';
import {Textarea} from 'primeng/textarea';
import {CreateExerciseDto} from '../../models/create-exercise.dto';
import {ExerciseContent} from '../../models/exercise-types/exercise-content.model';
import {ExercisesService} from '../../exercises.service';
import {MessageService} from 'primeng/api';

@Component({
  selector: 'app-create-exercise-form-modal',
  imports: [
    Dialog,
    ReactiveFormsModule,
    InputText,
    Select,
    MultipleChoiceFormComponent,
    FillInBlankFormComponent,
    Button,
    MultiSelect,
    ToggleSwitch,
    Textarea
  ],
  templateUrl: './create-exercise-form-modal.component.html',
  styleUrl: './create-exercise-form-modal.component.scss'
})
export class CreateExerciseFormModalComponent {


  @Input({required:true}) isVisible!:boolean;
  @Input({required: true}) lessons!: Lesson[];
  @Output() isVisibleChange = new EventEmitter<boolean>();

  private formBuilder = inject(FormBuilder);
  private exercisesService = inject(ExercisesService);
  private messageService = inject(MessageService);

  exerciseType = signal<ExerciseType | null>(null);

  exerciseTypes :{
    label: string;
    value: ExerciseType;
  }[]= [
    { label: 'Multiple Choice', value: 'MULTIPLE_CHOICE' },
    { label: 'Fill in blank', value:'FILL_IN_BLANK'}
  ];

  form : FormGroup = this.formBuilder.group({
    title:[
      '',
      [
        Validators.required,
        Validators.minLength(3),
      ]
    ],
    instructions:[
      '',
      [
        Validators.required,
        Validators.minLength(3),
      ]
    ],
    exerciseType:[
      null as ExerciseType | null,
      Validators.required
    ],
    content: this.formBuilder.group({}),
    lessons:[
      [] as String[],
    ],
    correctAnswers:[
      false
    ]
  })

  close(){
    this.isVisible = false;
    this.isVisibleChange.emit(false);
  }

  get content(): FormGroup{
    return this.form.get('content') as FormGroup;
  }

  createSentence(): FormGroup{
    return this.formBuilder.group({
      question: ['', Validators.required],
      answers: [[] as string[], Validators.required]
    });
  }

  onTypeChange(type: ExerciseType) {
    this.exerciseType.set(type);
    if (type === 'MULTIPLE_CHOICE') {
      this.form.setControl('content', this.formBuilder.group({
        question:[
          '',
          [
            Validators.required,
            Validators.minLength(3),
          ],
        ],
        options:[
          [] as String[],
          [
            Validators.required,
            Validators.minLength(2),
          ]
        ],
        correctAnswer:[
          ''
        ],
      }));
    }

    if (type === 'FILL_IN_BLANK') {
      this.form.setControl('content', this.formBuilder.group({
        sentences: this.formBuilder.array([this.createSentence()], Validators.minLength(1)),
      }));
    }
  }

  submit(){
    if(!this.form.valid) return;

    const { title, instructions, exerciseType, lessons, correctAnswers } = this.form.value;

    const createExerciseDto: CreateExerciseDto = {
      title,
      instructions,
      type: exerciseType.value,
      lessonIdList: lessons.map((lesson:Lesson) => lesson.id),
      correctAnswers:correctAnswers,
      content: this.content.value,
    };

    console.log("this is the create exercise dto: ",createExerciseDto);

    this.exercisesService.createExercise(createExerciseDto).subscribe({
      next: (res:Exercise)=>{
        console.log("Exercise Created Successfully");
        this.messageService.add({
          severity: 'success',
          summary: 'New Exercise is created successfully',
          detail: `Exercise has been created successfully related to lesson $ lessons.`,
        })
        this.close();
      }
    })

  }
}
