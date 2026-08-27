import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Exercise} from './models/exercise.model';

@Injectable({
  providedIn: 'root'
})
export class ExercisesService {

  private http = inject(HttpClient)
  private baseUrl = 'http://localhost:8080/api/exercise/';

  constructor() { }

  getExercises(courseId: string) {
    console.log("Fetch exercises");
    return this.http.get<Exercise[]>(this.baseUrl + `course/${courseId}`);
  }

}
