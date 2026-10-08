import {Component, inject} from '@angular/core';
import {Card} from 'primeng/card';
import {ActivatedRoute, Router} from '@angular/router';
import {ExerciseAttempt} from '../../models/exercise-attempt.model';
import {ExercisesAttemptService} from '../../services/exercises-attempt.service';
import {getExerciseTypeLabel} from '../../models/exercise.model';
import {
  MultipleChoiceContentComponent
} from '../../components/content/multiple-choice-content/multiple-choice-content.component';
import {
  FillInBlankContentComponent
} from '../../components/content/fill-in-blank-content/fill-in-blank-content.component';
import {
  MultipleChoiceAttemptFormComponent
} from '../../forms/multiple-choice-attempt-form/multiple-choice-attempt-form.component';
import {
  FillInBlankAttemptFormComponent
} from '../../forms/fill-in-blank-attempt-form/fill-in-blank-attempt-form.component';
import {Button} from 'primeng/button';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {FillInBlankContent} from '../../models/exercise-types/fill-in-blank.model';

@Component({
  selector: 'app-exercise-attempt',
  imports: [
    Card,
    MultipleChoiceContentComponent,
    FillInBlankContentComponent,
    MultipleChoiceAttemptFormComponent,
    FillInBlankAttemptFormComponent,
    Button,
    ReactiveFormsModule
  ],
  templateUrl: './exercise-attempt.component.html',
  styleUrl: './exercise-attempt.component.scss'
})
export class ExerciseAttemptComponent {

  private router = inject(Router);
  private exerciseAttemptService = inject(ExercisesAttemptService);
  private formBuilder = inject(FormBuilder);

  attempt!: ExerciseAttempt;

  form!: FormGroup ;

  constructor(private route:ActivatedRoute) {
  }

  ngOnInit():void {
    const id = this.route.snapshot.params['id'];

    if(!id){
      void this.router.navigate(['/']);
    }

    const passedAttempt = history.state?.['attempt'] as ExerciseAttempt | undefined;

    if(!passedAttempt){
      //TODO: fetch the exercise attempt by id
    }else{
      this.attempt = passedAttempt;
    }

    this.buildForm()
  }

  private buildForm(): void{
    this.form = this.formBuilder.group({
      exercise_type: [this.attempt.type],
      attempt: this.formBuilder.group({})
    })
    const exerciseType = this.attempt.type;
    switch(exerciseType){
      case "MULTIPLE_CHOICE":
        this.form.setControl('attempt',this.formBuilder.group({
          answer:['']
        }))
        break;
      case 'FILL_IN_BLANK':
        const content = this.attempt.content as FillInBlankContent;
        this.form.setControl('attempt',this.formBuilder.group({
          sentences: this.formBuilder.array(
            content.sentences.map(sentence =>
              this.formBuilder.group({
                sentenceId: [sentence.id],
                answer:['']
              })
            )
          )
        }))
        break;
    }
  }

  get attemptForm(): FormGroup{
    return this.form.get('attempt') as FormGroup;
  }


  protected readonly getExerciseTypeLabel = getExerciseTypeLabel;


  submit(){
    if(!this.form.valid) return;

    const {attempt} = this.form.value;

    console.log("this is the attempt submited",attempt)

  }
}
