import {Component, inject, OnInit, signal} from '@angular/core';
import {ExerciseInfosComponent} from '../../components/exercise-infos/exercise-infos.component';
import {Exercise} from '../../models/exercise.model';
import {ActivatedRoute, Router} from '@angular/router';
import {ExercisesService} from '../../exercises.service';

@Component({
  selector: 'app-exercise-details',
  imports: [
    ExerciseInfosComponent
  ],
  templateUrl: './exercise-details.component.html',
  styleUrl: './exercise-details.component.scss'
})
export class ExerciseDetailsComponent implements OnInit {

  private router = inject(Router);
  private exercisesService = inject(ExercisesService);

  exerciseId!: string;
  exercise = signal<Exercise | null>(null);
  exerciseIsLoading = signal<boolean>(false);

  constructor(private route:ActivatedRoute) {
  }

  ngOnInit(): void {
    const exerciseId = this.route.snapshot.paramMap.get('id');
    if (!exerciseId) {
      void this.router.navigate(['/not-found']);
      return;
    }
    this.exerciseId = exerciseId;
    this.loadExercise()
  }

  loadExercise(){
    if(!this.exerciseId) return;

    this.exerciseIsLoading.set(true);
    this.exercisesService.getExerciseById(this.exerciseId).subscribe({
      next:(data:Exercise) =>{
        console.log("Exercise fetched successfully");
        console.log("the data of the exercise: ", data);
        this.exercise.set(data);
        this.exerciseIsLoading.set(false);
      },
      error:(error)=>{
        this.exerciseIsLoading.set(false);
        console.log('An error appeared during exercise fetching',error.message);
      }
    })

  }



}
