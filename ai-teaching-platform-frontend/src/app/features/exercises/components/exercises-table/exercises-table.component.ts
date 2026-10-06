import {Component, Input, OnInit} from '@angular/core';
import {Card} from 'primeng/card';
import {TableModule} from 'primeng/table';
import {Message} from 'primeng/message';
import {Skeleton} from 'primeng/skeleton';
import {Exercise, ExerciseType} from '../../models/exercise.model';
import {Lesson} from '../../models/lesson.model';

@Component({
  selector: 'app-exercises-table',
  imports: [
    Card,
    TableModule,
    Message,
    Skeleton
  ],
  templateUrl: './exercises-table.component.html',
  styleUrl: './exercises-table.component.scss'
})
export class ExercisesTableComponent {

  @Input({required: true}) maxRows!: number;
  @Input({required: true}) exercises!: Exercise[];
  @Input({required: true}) loading!: boolean;


  getExerciseTypeLabel(type: ExerciseType): string {
    const labels: Record<ExerciseType, string> = {
      MULTIPLE_CHOICE: 'Multiple Choice',
      FILL_IN_BLANK: 'Fill in the Blank'
    };

    return labels[type];
  }

  printLessons(lessons: Lesson[]) {
    let lessonsString = "";

    lessons.forEach(lesson => {
      lessonsString += lesson.title + " ";
    });

    return lessonsString;
  }

}
