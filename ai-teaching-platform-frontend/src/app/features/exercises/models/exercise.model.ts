export interface Lesson {
  id: number;
  title: string;
}

export interface Exercise {
  id: string;
  title: string;
  type: string;
  lesson: Lesson[];
}
