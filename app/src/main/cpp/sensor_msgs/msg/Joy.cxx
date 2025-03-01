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
 * @file Joy.cpp
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

#include "Joy.h"
#include <fastcdr/Cdr.h>

#include <fastcdr/exceptions/BadParamException.h>
using namespace eprosima::fastcdr::exception;

#include <utility>


namespace sensor_msgs {

namespace msg {





Joy::Joy()
{
}

Joy::~Joy()
{
}

Joy::Joy(
        const Joy& x)
{
    m_header = x.m_header;
    m_axes = x.m_axes;
    m_buttons = x.m_buttons;
}

Joy::Joy(
        Joy&& x) noexcept
{
    m_header = std::move(x.m_header);
    m_axes = std::move(x.m_axes);
    m_buttons = std::move(x.m_buttons);
}

Joy& Joy::operator =(
        const Joy& x)
{

    m_header = x.m_header;
    m_axes = x.m_axes;
    m_buttons = x.m_buttons;
    return *this;
}

Joy& Joy::operator =(
        Joy&& x) noexcept
{

    m_header = std::move(x.m_header);
    m_axes = std::move(x.m_axes);
    m_buttons = std::move(x.m_buttons);
    return *this;
}

bool Joy::operator ==(
        const Joy& x) const
{
    return (m_header == x.m_header &&
           m_axes == x.m_axes &&
           m_buttons == x.m_buttons);
}

bool Joy::operator !=(
        const Joy& x) const
{
    return !(*this == x);
}

/*!
 * @brief This function copies the value in member header
 * @param _header New value to be copied in member header
 */
void Joy::header(
        const std_msgs::msg::Header& _header)
{
    m_header = _header;
}

/*!
 * @brief This function moves the value in member header
 * @param _header New value to be moved in member header
 */
void Joy::header(
        std_msgs::msg::Header&& _header)
{
    m_header = std::move(_header);
}

/*!
 * @brief This function returns a constant reference to member header
 * @return Constant reference to member header
 */
const std_msgs::msg::Header& Joy::header() const
{
    return m_header;
}

/*!
 * @brief This function returns a reference to member header
 * @return Reference to member header
 */
std_msgs::msg::Header& Joy::header()
{
    return m_header;
}


/*!
 * @brief This function copies the value in member axes
 * @param _axes New value to be copied in member axes
 */
void Joy::axes(
        const std::vector<float>& _axes)
{
    m_axes = _axes;
}

/*!
 * @brief This function moves the value in member axes
 * @param _axes New value to be moved in member axes
 */
void Joy::axes(
        std::vector<float>&& _axes)
{
    m_axes = std::move(_axes);
}

/*!
 * @brief This function returns a constant reference to member axes
 * @return Constant reference to member axes
 */
const std::vector<float>& Joy::axes() const
{
    return m_axes;
}

/*!
 * @brief This function returns a reference to member axes
 * @return Reference to member axes
 */
std::vector<float>& Joy::axes()
{
    return m_axes;
}


/*!
 * @brief This function copies the value in member buttons
 * @param _buttons New value to be copied in member buttons
 */
void Joy::buttons(
        const std::vector<int32_t>& _buttons)
{
    m_buttons = _buttons;
}

/*!
 * @brief This function moves the value in member buttons
 * @param _buttons New value to be moved in member buttons
 */
void Joy::buttons(
        std::vector<int32_t>&& _buttons)
{
    m_buttons = std::move(_buttons);
}

/*!
 * @brief This function returns a constant reference to member buttons
 * @return Constant reference to member buttons
 */
const std::vector<int32_t>& Joy::buttons() const
{
    return m_buttons;
}

/*!
 * @brief This function returns a reference to member buttons
 * @return Reference to member buttons
 */
std::vector<int32_t>& Joy::buttons()
{
    return m_buttons;
}




} // namespace msg


} // namespace sensor_msgs
// Include auxiliary functions like for serializing/deserializing.
#include "JoyCdrAux.ipp"

