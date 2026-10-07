import {Component, Input, OnInit} from '@angular/core';
import {Card} from 'primeng/card';
import {TableModule} from 'primeng/table';
import {Message} from 'primeng/message';
import {Skeleton} from 'primeng/skeleton';
import {Exercise, getExerciseTypeLabel} from '../../models/exercise.model';
import {Lesson} from '../../models/lesson.model';
import {Button} from 'primeng/button';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-exercises-table',
  imports: [
    Card,
    TableModule,
    Message,
    Skeleton,
    Button,
    RouterLink
  ],
  templateUrl: './exercises-table.component.html',
  styleUrl: './exercises-table.component.scss'
})
export class ExercisesTableComponent {

  @Input({required: true}) maxRows!: number;
  @Input({required: true}) exercises!: Exercise[];
  @Input({required: true}) loading!: boolean;



  printLessons(lessons: Lesson[]) {
    let lessonsString = "";

    lessons.forEach(lesson => {
      lessonsString += lesson.title + " ";
    });

    return lessonsString;
  }

  protected readonly getExerciseTypeLabel = getExerciseTypeLabel;
}
