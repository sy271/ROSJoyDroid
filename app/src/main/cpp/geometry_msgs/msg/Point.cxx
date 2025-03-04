// Copyright 2016 Proyectos y Sistemas de Mantenimiento SL (eProsima).
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/*!
 * @file Point.cpp
 * This source file contains the implementation of the described types in the IDL file.
 *
 * This file was generated by the tool fastddsgen.
 */

#ifdef _WIN32
// Remove linker warning LNK4221 on Visual Studio
namespace {
char dummy;
}  // namespace
#endif  // _WIN32

#include "Point.h"
#include <fastcdr/Cdr.h>

#include <fastcdr/exceptions/BadParamException.h>
using namespace eprosima::fastcdr::exception;

#include <utility>

namespace geometry_msgs {

    namespace msg {

        Point::Point()
        {
            // m_x = 0.0;
            // m_y = 0.0;
            // m_z = 0.0;

            // Just to register all known types
//            registerPointTypes();
        }

        Point::~Point()
        {
        }

        Point::Point(
                const Point& x)
        {
            m_x = x.m_x;
            m_y = x.m_y;
            m_z = x.m_z;
        }

        Point::Point(
                Point&& x) noexcept
        {
            m_x = x.m_x;
            m_y = x.m_y;
            m_z = x.m_z;
        }

        Point& Point::operator =(
                const Point& x)
        {
            m_x = x.m_x;
            m_y = x.m_y;
            m_z = x.m_z;

            return *this;
        }

        Point& Point::operator =(
                Point&& x) noexcept
        {
            m_x = x.m_x;
            m_y = x.m_y;
            m_z = x.m_z;

            return *this;
        }

        bool Point::operator ==(
                const Point& x) const
        {
            return (m_x == x.m_x &&
                    m_y == x.m_y &&
                    m_z == x.m_z);
        }

        bool Point::operator !=(
                const Point& x) const
        {
            return !(*this == x);
        }

        size_t Point::getMaxCdrSerializedSize(
                size_t current_alignment)
        {
            size_t initial_alignment = current_alignment;

            current_alignment += 8 + eprosima::fastcdr::Cdr::alignment(current_alignment, 8);
            current_alignment += 8 + eprosima::fastcdr::Cdr::alignment(current_alignment, 8);
            current_alignment += 8 + eprosima::fastcdr::Cdr::alignment(current_alignment, 8);

            return current_alignment - initial_alignment;
        }

        size_t Point::getCdrSerializedSize(
                const Point& /*data*/,
                size_t current_alignment)
        {
            size_t initial_alignment = current_alignment;

            current_alignment += 8 + eprosima::fastcdr::Cdr::alignment(current_alignment, 8);
            current_alignment += 8 + eprosima::fastcdr::Cdr::alignment(current_alignment, 8);
            current_alignment += 8 + eprosima::fastcdr::Cdr::alignment(current_alignment, 8);

            return current_alignment - initial_alignment;
        }

        void Point::serialize(
                eprosima::fastcdr::Cdr& scdr) const
        {
            scdr << m_x;
            scdr << m_y;
            scdr << m_z;
        }

        void Point::deserialize(
                eprosima::fastcdr::Cdr& dcdr)
        {
            dcdr >> m_x;
            dcdr >> m_y;
            dcdr >> m_z;
        }

/*!
 * @brief This function sets a value in member x
 * @param _x New value for member x
 */
        void Point::x(
                double _x)
        {
            m_x = _x;
        }

/*!
 * @brief This function returns the value of member x
 * @return Value of member x
 */
        double Point::x() const
        {
            return m_x;
        }

/*!
 * @brief This function returns a reference to member x
 * @return Reference to member x
 */
        double& Point::x()
        {
            return m_x;
        }

/*!
 * @brief This function sets a value in member y
 * @param _y New value for member y
 */
        void Point::y(
                double _y)
        {
            m_y = _y;
        }

/*!
 * @brief This function returns the value of member y
 * @return Value of member y
 */
        double Point::y() const
        {
            return m_y;
        }

/*!
 * @brief This function returns a reference to member y
 * @return Reference to member y
 */
        double& Point::y()
        {
            return m_y;
        }

/*!
 * @brief This function sets a value in member z
 * @param _z New value for member z
 */
        void Point::z(
                double _z)
        {
            m_z = _z;
        }

/*!
 * @brief This function returns the value of member z
 * @return Value of member z
 */
        double Point::z() const
        {
            return m_z;
        }

/*!
 * @brief This function returns a reference to member z
 * @return Reference to member z
 */
        double& Point::z()
        {
            return m_z;
        }

        size_t Point::getKeyMaxCdrSerializedSize(
                size_t current_alignment)
        {
            size_t current_align = current_alignment;

            return current_align;
        }

        bool Point::isKeyDefined()
        {
            return false;
        }

        void Point::serializeKey(
                eprosima::fastcdr::Cdr& /*scdr*/) const
        {
            // No key defined for this type
        }

    } // namespace msg

} // namespace geometry_msgs

// Include auxiliary functions like for serializing/deserializing.
#include "PointCdrAux.ipp"
