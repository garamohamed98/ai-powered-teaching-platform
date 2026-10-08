import {Component, inject, Input, OnInit, signal} from '@angular/core';
import {ExercisesTableComponent} from '../../components/exercises-table/exercises-table.component';
import {ExercisesService} from '../../services/exercises.service';
import {Exercise} from '../../models/exercise.model';
import {Button} from 'primeng/button';
import {
  CreateExerciseFormModalComponent
} from '../../components/create-exercise-form-modal/create-exercise-form-modal.component';
import {Lesson} from '../../models/lesson.model';
import {
  GenerateExerciseFormModalComponent
} from '../../components/generate-exercise-form-modal/generate-exercise-form-modal.component';

@Component({
  selector: 'app-course-exercises-list',
  imports: [
    ExercisesTableComponent,
    Button,
    CreateExerciseFormModalComponent,
    GenerateExerciseFormModalComponent
  ],
  templateUrl: './course-exercises-list.component.html',
  styleUrl: './course-exercises-list.component.scss'
})
export class CourseExercisesListComponent implements OnInit {

  @Input({required:true}) courseId!: string;
  @Input({required:true}) lessons!: Lesson[];

  private exerciseService = inject(ExercisesService);

  exercises = signal<Exercise[]>([])
  maxRows:number = 6;
  loading = signal<boolean>(false);
  isCreateExerciseModalVisible = signal<boolean>(false);
  isGenerateExerciseModalVisible = signal<boolean>(false);

  ngOnInit() {
    this.loadExercises();
  }

  loadExercises() {
    if(this.courseId == "") return;
    this.loading.set(true);
    this.exercises.set(Array(this.maxRows).fill({}));
    this.exerciseService
      .getExercises(this.courseId)
      .subscribe({
        next: (data:Exercise[])=>{
          console.log("Exercise list fetched successfully");
          console.log("this is the exercise data",data);
          this.exercises.set(data);
          console.log("this is the loaded exercise",this.exercises());
          this.loading.set(false);
        },
        error: (error:any)=>{
          this.loading.set(false);
          this.exercises.set([]);
          console.log(
            "An error appeared during exercise list fetching:",
            error.message
          );
        }
      })
  }

  showCreateExerciseModal(){
    this.isCreateExerciseModalVisible.set(true);
  }

  showGenerateExerciseModal(){
    this.isGenerateExerciseModalVisible.set(true);
  }
}
