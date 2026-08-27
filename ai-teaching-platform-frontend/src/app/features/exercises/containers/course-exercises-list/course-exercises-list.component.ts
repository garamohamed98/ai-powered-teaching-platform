import {Component, inject, Input, OnInit, signal} from '@angular/core';
import {ExercisesTableComponent} from '../../components/exercises-table/exercises-table.component';
import {ExercisesService} from '../../exercises.service';
import {Exercise} from '../../models/exercise.model';

@Component({
  selector: 'app-course-exercises-list',
  imports: [
    ExercisesTableComponent
  ],
  templateUrl: './course-exercises-list.component.html',
  styleUrl: './course-exercises-list.component.scss'
})
export class CourseExercisesListComponent implements OnInit {

  @Input({required:true}) courseId!: string;

  private exerciseService = inject(ExercisesService);

  exercises = signal<Exercise[]>([])
  maxRows:number = 6;
  loading = signal<boolean>(false);

  ngOnInit() {
    this.loadExercises(this.courseId);
  }

  loadExercises(courseId: string) {
    if(courseId == "") return;
    this.loading.set(true);
    this.exercises.set(Array(this.maxRows).fill({}));
    this.exerciseService
      .getExercises(courseId)
      .subscribe({
        next: (data:Exercise[])=>{
          console.log("Exercise list fetched successfully");
          this.exercises.set(data);
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
}
