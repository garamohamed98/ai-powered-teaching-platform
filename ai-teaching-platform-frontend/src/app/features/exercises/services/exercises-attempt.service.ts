import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ExerciseAttempt} from '../models/exercise-attempt.model';

@Injectable({
  providedIn: 'root'
})
export class ExercisesAttemptService {

  private http = inject(HttpClient)
  private baseUrl = 'http://localhost:8080/api/exercise-attempt';

  constructor() { }

  startExerciseAttempt(exerciseId: string){
    console.log("creating exercise attempt");
    return this.http.post<ExerciseAttempt>(
      `${this.baseUrl}/${exerciseId}/attempt`,
      null
    );
  }

}
