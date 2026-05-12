import { constantsService } from './storage'

export const getRecitationOptions = async (subject, grade) => {
  return constantsService.getRecitationOptions(subject, grade)
}

export const getRecitationById = async (id) => {
  return constantsService.getRecitationById(id)
}

export const textbookRecitations = {}
