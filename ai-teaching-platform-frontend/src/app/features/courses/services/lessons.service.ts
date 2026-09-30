import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Lesson} from '../models/lesson.model';

@Injectable({
  providedIn: 'root'
})
export class LessonsService {

  private http = inject(HttpClient);
  private baseUrl = "http://localhost:8080/api/lesson"

  constructor() {}

  getLesson(lessonId:string){
    console.log("fetching lesson id: " + lessonId);
    return this.http.get<Lesson>(this.baseUrl+"/" + lessonId);
  }

  updateLessonContent(lessonId:string,content:string){
    const url = `${this.baseUrl}/${lessonId}/content`;
    const body ={
      content: content
    };
    return this.http.patch<Lesson>(url,body);
  }

}
